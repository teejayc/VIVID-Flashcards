<?php

	require "init.php";

	$user_id = $_POST["user_id"];
	$user_pass = $_POST["user_pass"];

	$sql_query = "SELECT user_name FROM user_info WHERE user_id like '$user_id' and user_pass like '$user_pass';";

	$result = mysqli_query($con, $sql_query);

	if (mysqli_num_rows($result) > 0) {
		$row = mysqli_fetch_assoc($result);
		$user_name = $row["user_name"];
		echo "Login success! Hello, " . $user_id . "!";
	}
	else {
		echo "Login failed! Please check your ID and password.";
	}

		mysqli_close($con);

?>
