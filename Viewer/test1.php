<?php

$redis = new redis(); 
$redis->connect('localhost', 6379); 

$info = $redis->info(); 
echo "<pre>"; 
print_r($info); 
echo "</pre>"; 


echo $redis ->hget('broadcaster', 'email');

?> 
