scripts_dir=home/abhishek/Desktop/major/scripts
chunk_dir

if [! -d chunk ]
then
  echo "ERROR: failed to locate directory chunk!"
  return 1
fi

return 0
