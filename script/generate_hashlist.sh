##############################################################
# change this path according to machine
cur_dir=/home/abhishek/Desktop/major

##############################################################

script_dir=${cur_dir}/script
chunk_dir=${cur_dir}/temp_chunk
metadata_dir=${cur_dir}/temp_metadata

# echo "cur_dir: $cur_dir "
# echo "script_dir : $script_dir"
# echo "chunk_dir : $chunk_dir"
# echo "metadata_dir : $metadata_dir"


if [ ! -d $chunk_dir ]
then
  echo "ERROR: failed to locate directory chunk!"
  return 1
fi

if [ $# -ne 1 ]
then
  echo "ERROR: Invalid number of parameters!"
  return 2
fi

inputfile=$1
hash_list=${metadata_dir}/${inputfile}.hashlist
chunk_list=${metadata_dir}/${inputfile}.chunklist

if [ -f $hash_list ]
then
  rm $hash_list
fi

if [ -f $chunk_list ]
then
  rm $chunk_list
fi


ls $chunk_dir | sort -n > $chunk_list

chunk_no=1
no_of_chunks=`cat $chunk_list |  wc -l`


while [ $chunk_no -le $no_of_chunks ]
do
  chunk_name=`sed -n "${chunk_no}p" $chunk_list`
  chunk_name=${chunk_dir}/${chunk_name}

  hash_val=`java -classpath $script_dir hash $chunk_name`

  # echo $hash_val

  echo $hash_val >> $hash_list

  new_name=${chunk_dir}/${hash_val}
  mv $chunk_name $new_name

  chunk_no=`expr $chunk_no + 1`

done

rm $chunk_list

return 0
