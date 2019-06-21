#post_workflow

Submit="/home/hpcsw/CD-adapco/13.06.011-R8/STAR-CCM+13.06.011-R8/star/bin/starccm+ -power -cpubind -hardwarebatch -np 16 -renderthreads 16 "
java="-batch /mnt/hpcdata/CFDCentralStorage/Utilities/Fuchao/post_workflow/post_workflow.java"
python="/usr/local/python3/bin/python3.7"
scriptpy="/mnt/hpcdata/CFDCentralStorage/Utilities/Fuchao/autoReport/analyze_ppt_replace_linux.py"

$Submit $java $1|tee ${1%.sim}".postlog"
echo "Done Starccm+ post!!!"
sleep 2
#echo ${1%.sim}".post"
$python $scriptpy ${1%.sim}".post"



