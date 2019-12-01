<?php 
$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');
$name=$_REQUEST['Name']; 
$floor=$_REQUEST['Floor']; 
$building_ID=$_REQUEST['Building_ID']; 
$desc = $_REQUEST['Description'];


$name=(String)$name;
$sql=mysqli_query($con,"insert into Room(Name, Floor, Description, Building_ID) values ('$name','$floor','$desc','$building_ID');");
if($sql){
    $last_id = $con->insert_id;
    echo $last_id;
}else{
    die(mysql_error());
}

mysqli_close($con);
?>