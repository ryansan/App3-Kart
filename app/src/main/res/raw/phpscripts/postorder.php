<?php
$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');
$orderDate=$_REQUEST['OrderDate'];
$date = date("Y-m-d", substr($orderDate, 0, 10));
$fromHour=$_REQUEST['FromHour']; 
$toHour=$_REQUEST['ToHour']; 
$name =$_REQUEST['Name']; 
$description =$_REQUEST['Description']; 
$mail =$_REQUEST['Mail']; 
$room_ID =$_REQUEST['Room_ID']; 

$sql=mysqli_query($con,"insert into Orders(OrderDate, FromHour, ToHour, Name, Description, Mail, Room_ID) values('$date','$fromHour','$toHour','$name','$description','$mail','$room_ID');");
if($sql){
    $last_id = $con->insert_id;
    echo $last_id;
}else{
    die(mysql_error());
}
 mysqli_close($con);
?>