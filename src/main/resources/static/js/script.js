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

