
<?php

	$db_name = "vivid_flashcards";
	$host = "root";
	$host_pass = "itbD13e4gab.";
	$server_name = "162.243.102.106";

	$con = mysqli_connect($server_name, $host, $host_pas, $db_name);

	if (!$con) {
		echo "Connection Error! ".mysqli_connect_error();
	}
	else {
		echo "<h3>Database Connection Success...</h3>";
	}

?>
