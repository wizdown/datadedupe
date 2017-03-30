##############################################################
# change this path according to machine
cur_dir=/home/abhishek/Desktop/major
server_cur_dir=/root/major

##############################################################

if [ $# -ne 1 ]
then
    echo "Invalid Params!"
    exit 2
fi

inputfile=$1

server_temp_metadata_dir=${server_cur_dir}/temp_metadata

chunk_dir=${cur_dir}/temp_chunk
metadata_dir=${cur_dir}/temp_metadata

old_chunk_list=${metadata_dir}/${inputfile}.hashlist.old

if [ -f $old_chunk_list ]
then
    echo "DEBUG: Duplicate chunks found! Deleting them!"
    while read chunkName
    do
      echo "DEBUG: Deleting $chunkName"
      chunkName=${chunk_dir}/${chunkName}
      rm $chunkName
    done < $old_chunk_list
else
  # All chunks are new. No duplicate chunk found!
  echo "DEBUG: No duplicate chunks found!"
fi
