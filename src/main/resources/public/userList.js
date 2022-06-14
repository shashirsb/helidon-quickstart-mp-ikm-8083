function loadTable() {

if(getCookie("sessionUUid") != null && getCookie("userId") != null && getCookie("sessionUUid") != "" && getCookie("userId") != ""){
		
	console.log("insideLoadTable");

	var request;
	request = $.ajax({
		url: "http://150.136.116.225:30994/user/getUsers",
		type: "GET",
		async: false,
		dataType: "json",
		success: function() {
			console.log("Json Data Fetched ");
		}

	});

	request.done(function(jqXHR, textStatus, response) {

		var d = JSON.stringify(response);
		var s = $.parseJSON(d);

		var ss = s.responseJSON;

		$(document).ready(function() {
			$('#example').DataTable({
				processing: true,
				data: ss,
				columns: [
					{ "data": "id" },
					{ "data": "firstName" },
					{ "data": "lastName" },
					{ "data": "email" },
					{ "data": "phone" },
					{ "data": "createdDate" }
				]

			});
		});
	});
	} else {
		alert("Invalid Session - Kindly Login");
		window.location.href = "http://150.136.116.225:30996/login.html";
	}

}

function getCookie(cname) {
	let name = cname + "=";
	let decodedCookie = decodeURIComponent(document.cookie);
	let ca = decodedCookie.split(';');
	for (let i = 0; i < ca.length; i++) {
		let c = ca[i];
		while (c.charAt(0) == ' ') {
			c = c.substring(1);
		}
		if (c.indexOf(name) == 0) {
			return c.substring(name.length, c.length);
		}
	}
	return "";
}