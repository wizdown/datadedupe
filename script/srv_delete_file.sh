##############################################################
# change this path according to machine
server_cur_dir=/root/major

##############################################################


if [ $# -ne 1 ]
then
    echo "Invalid Params!"
    exit 2
fi

server_chunk_dir=${server_cur_dir}/chunk
server_metadata_dir=${server_cur_dir}/metadata
hash_freq_map=${server_metadata_dir}/hash_freq.list
server_temp_metadata_dir=${server_cur_dir}/temp_metadata
server_metadata_dir_files=${server_cur_dir}/metadata/files

inputfile=$1
inputfile_hashlist=${server_metadata_dir_files}/${inputfile}

# echo $inputfile_hashlist

###########################################################
#FUNCTIONS BELOW
function getChunkFreq {
  if [ $# -ne 1 ]
  then
      echo "Invalid Params!"
      exit 3
  fi

  key=$1
  freq=`sed -n "s/$key:\(.*\)/\1/p" $hash_freq_map`

  echo $freq
}

##########################################################
#MAINS BELOW

while read chunkName
do
  # echo "Chunkname : $chunkName "

  freq=`getChunkFreq $chunkName`

  if [ $freq -eq 1 ]
  then
    chunk_file=${server_chunk_dir}/${chunkName}
   rm $chunk_file
    sed -i "/$chunkName/d" $hash_freq_map
  else
    new_freq=`expr $freq - 1`
    sed -i "s/${chunkName}.*/${chunkName}:${new_freq}/" $hash_freq_map
  fi
done < $inputfile_hashlist

rm $inputfile_hashlist
