if [ $# -ne 1 ]
then
  echo "ERROR: Incorrect params!"
  exit  1
fi

echo "Running script"
echo "Param received : $1 "
echo "Files : "
ls
