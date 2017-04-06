##############################################################
# change this path according to machine
server_cur_dir=/root/major

##############################################################

if [ $# -ne 1 ]
then
    echo "Invalid Params!"
    exit 2
fi

inputfile=$1

server_chunk_dir=${server_cur_dir}/chunk
server_metadata_dir=${server_cur_dir}/metadata
hash_freq_map=${server_metadata_dir}/hash_freq.list
server_temp_metadata_dir=${server_cur_dir}/temp_metadata
server_metadata_dir_files=${server_cur_dir}/metadata/files


inputfile_old_loc=${server_temp_metadata_dir}/${inputfile}.hashlist
inputfile_new_loc=${server_metadata_dir_files}/${inputfile}

old_chunk_list=${server_temp_metadata_dir}/${inputfile}.hashlist.old
new_chunk_list=${server_temp_metadata_dir}/${inputfile}.hashlist.new

if [ -f $old_chunk_list ]
then
  echo "Processing metadata corresponding to duplicate chunks!"
  while read chunkName
  do

    old_freq_val=`sed -n "s/$chunkName:\(.*\)/\1/p" $hash_freq_map`
    new_freq_val=`expr $old_freq_val + 1`
    # echo "DEBUG: Old freq val => $old_freq_val"
    # echo "DEBUG: New freq val => $new_freq_val"
    sed -i "s/${chunkName}:${old_freq_val}/${chunkName}:${new_freq_val}/" $hash_freq_map

  done < $old_chunk_list

fi

if [ -f $new_chunk_list ]
then
  echo "Processing metadata corresponding to new chunks!"
  while read chunkName
  do

    echo "$chunkName:1" >> $hash_freq_map

  done < $new_chunk_list

fi

echo "Processing hashlist metadata!"
mv $inputfile_old_loc $inputfile_new_loc
