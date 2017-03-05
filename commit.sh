if [ $# -ne 1 ]
then
	echo "Provide commit message"
	exit 1
fi
git add *
git commit -m"$1"
git push origin master
