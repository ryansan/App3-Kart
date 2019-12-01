<?php 
$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');
$name=$_REQUEST['Name']; 
$address=$_REQUEST['Address']; 
$latitude =$_REQUEST['Latitude']; 
$longitude =$_REQUEST['Longitude']; 

$name=(String)$name;
$sql=mysqli_query($con,"insert into Building(Name, Address, Latitude, Longitude) values ('$name','$address','$latitude','$longitude');");
if($sql){
    $last_id = $con->insert_id;
    echo $last_id;
}else{
    die(mysql_error());
}
 mysqli_close($con);
?>