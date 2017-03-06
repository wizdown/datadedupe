server_cur_dir=/root/major


server_chunk_dir=${server_cur_dir}/chunk
server_script_dir=${server_cur_dir}/script
server_metadata_dir=${server_cur_dir}/metadata
hash_freq_map=${server_metadata_dir}/hash_freq.list
server_temp_metadata_dir=${server_cur_dir}/temp_metadata


##########################################################
# FUNCTIONS BELOW

function verify_hash {
  # 0 signifies that hash is present
  # 1 signifies that hash is not present
  # Assuming that input is always given
  if [ $# -ne 1 ]
  then
    echo "ERROR-verify_hash() : Invalid params!"
    exit 2
  fi

  key=$1
  if [ ! -f $hash_freq_map ]
  then
        echo 1
	      return 1
  fi

  cut -d':' -f1 $hash_freq_map | grep -w $key

  if [ $? -eq 0 ]
  then
    echo 0
    return 0
  else
    echo 1
    return 1
  fi
}


##########################################################
# MAINS BELOW


if [ $# -ne 1 ]
then
	echo "ERROR: Incorrect number of paramters!"
	return 1
fi


hashlist=$1
hashlist=${server_temp_metadata_dir}/${inputfile}.hashlist
echo $hashlist

new_chunk_file=${server_temp_metadata_dir}/${hashlist}.new
old_chunk_file=${server_temp_metadata_dir}/${hashlist}.old

if [ -f $new_chunk_file ]
then
   rm $new_chunk_file
fi

if [ -f $old_chunk_file ]
then
   rm $old_chunk_file
fi

hash_no=1
total_hash=`cat $hashlist | wc -l `

while [ $hash_no -le $total_hash ]
do

  hash_name=`sed -n "${hash_no}p" $hashlist`
  status=`verify_hash $hash_name`
  if [ $status -eq 0 ]
  then
    echo $hash_name >> $old_chunk_file
  else
    echo $hash_name >> $new_chunk_file
  fi

  hash_no=`expr $hash_no + 1`

done
