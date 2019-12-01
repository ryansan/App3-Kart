<?php 

$con=mysqli_connect('student.cs.hioa.no','s326149','','s326149');
$sql=("select * from Building"); 

$tabell=mysqli_query($con,$sql);

while ($row=mysqli_fetch_assoc($tabell)) {
$output[]=$row;
}
print(json_encode($output)); 
mysqli_close($con);
?>