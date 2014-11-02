<?php
try
{
  $redis = new Redis();
  echo 1111;
}
catch(Exception $e)
{
  echo "$redis"; 
$redis->connect('127.0.0.1', 6379); 
echo  "asdasd";
}
$info = $redis->info(); 
  
?> 
