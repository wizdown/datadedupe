script_dir=/home/abhishek/Desktop/major/scripts
chunk_dir=/home/abhishek/Desktop/major/chunk
cur_dir=/home/abhishek/Desktop/major

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
hash_list=${cur_dir}/${inputfile}.hashlist
chunk_list=${cur_dir}/${inputfile}.chunklist

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

  java -classpath $script_dir hash $chunk_name >> $hash_list
  chunk_no=`expr $chunk_no + 1`

done


return 0
