<?php
$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');

$ID =$_REQUEST['ID']; 

$sql=mysqli_query($con,"DELETE FROM Room WHERE ID='$ID';");
if($sql){
    echo "delete ok";
}else{
    die(mysql_error());
}
 mysqli_close($con);
?>