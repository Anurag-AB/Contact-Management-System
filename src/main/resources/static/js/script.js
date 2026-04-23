console.log("this is script file");

const toggleSidebar = () => {

    if ($(".sidebar").is(":visible")) {

        // close sidebar
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");

    } else {

        // show sidebar
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
}

function confirmDelete(id) {
    if (confirm("Are you sure you want to delete this user?")) {
        window.location.href = "/admin/delete-user/" + id;
    }
}

const search = () => {

	let query = $("#search-input").val().trim();

	if (query == '') {
		$(".search-result").hide();
	} else {

		let url = `http://localhost:8083/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/${contact.cId}/contact' class="list-group-item list-group-item-action">${contact.name}</a>`;
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});
	}
}


