#!/usr/bin/env bash

# Change this if you're making your own fork with a different namespace
app_id="cl.emilym.sinatra"

update_property() {
  local file="$1"
  local property="$2"
  local value="$3"

  if grep -q "^$property=" "$file"; then
    awk -F= -v prop="$property" -v val="$value" '
      BEGIN { OFS = "=" }
      $1 == prop {
        $0 = prop "=" val
        updated = 1
      }
      { print }
      END { if (!updated) print prop, val }
    ' "$file" > "$file.tmp" && mv "$file.tmp" "$file"
  else
    echo "$property=$value" >> "$file"
  fi
}

index_of() {
  local needle="$1"
  shift 1
  local -a haystack=("$@")

  for i in "${!haystack[@]}"
  do
    if [[ "${haystack[$i]}" == "$needle" ]]; then
      echo "${i}"
      return 0
    fi
  done

  echo "-1"
  return 0
}

contains() {
  local i=$(index_of "$@")

  if [[ "$i" -ne "-1" ]]; then
    return 0
  fi
  return 1
}

read -p "Enter a Firebase project ID: " project_id

read -p "Enter a Google Maps API Key: " maps_api_key

read -p "Enter a server url [https://develop-api.sinatra-transport.com/canberra/]: " api_endpoint
api_endpoint="${api_endpoint:-https://develop-api.sinatra-transport.com/canberra/}"

app_list="$(firebase apps:list --non-interactive --project "$project_id" --json)"

app_namespaces=()
while IFS= read -r line; do
    app_namespaces+=("$line")
done < <(jq -r '.result[] | "\(.namespace) \(.platform)"' <<< "$app_list")

app_ids=()
while IFS= read -r line; do
    app_ids+=("$line")
done < <(jq -r '.result[].appId' <<< "$app_list")

if ! $(contains "$app_id.develop ANDROID" "${app_namespaces[@]}") || ! $(contains "$app_id ANDROID" "${app_namespaces[@]}") || ! $(contains "$app_id.develop IOS" "${app_namespaces[@]}") || ! $(contains "$app_id IOS" "${app_namespaces[@]}") ; then
  echo "Firebase project is missing $app_id.develop or $app_id namespaces for either Android or iOS"
fi

mkdir -p androidApp/src/debug
mkdir -p androidApp/src/main
mkdir -p iosApp/iosApp/Firebase

firebase apps:sdkconfig --non-interactive --project "$project_id" ANDROID "${app_ids[$(index_of "cl.emilym.sinatra.develop ANDROID" "${app_namespaces[@]}")]}" > androidApp/src/debug/google-services.json
firebase apps:sdkconfig --non-interactive --project "$project_id" ANDROID "${app_ids[$(index_of "cl.emilym.sinatra ANDROID" "${app_namespaces[@]}")]}" > androidApp/src/main/google-services.json
firebase apps:sdkconfig --non-interactive --project "$project_id" IOS "${app_ids[$(index_of "cl.emilym.sinatra.develop IOS" "${app_namespaces[@]}")]}" > iosApp/iosApp/Firebase/GoogleService-Info-Debug.plist
firebase apps:sdkconfig --non-interactive --project "$project_id" IOS "${app_ids[$(index_of "cl.emilym.sinatra IOS" "${app_namespaces[@]}")]}" > iosApp/iosApp/Firebase/GoogleService-Info-Release.plist

touch secrets.properties

update_property secrets.properties "MAPS_API_KEY" "$maps_api_key"
update_property gradle.properties "apiUrl" "$api_endpoint"