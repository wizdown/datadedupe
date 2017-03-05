if [ $# -ne 1 ]
then
	echo "Provide commit message"
	exit 1
fi
echo $@
git add *
git commit -m"$@"
git push origin master
