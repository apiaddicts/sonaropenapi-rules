if [ -z "$1" ]
  then
    echo "No argument supplied. Correct use: clean.sh <path>"
else
  find $1 -type f -name "*.yaml" -exec sed -i "/^\s*$/d" {} \;
fi

