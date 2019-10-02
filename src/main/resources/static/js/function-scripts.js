function searchingFunction() {
    var input, filter, table, tr, td, i, txtValue;
    input = document.getElementById("searchingFields");
    filter = input.value.toUpperCase();
    table = document.getElementById("listTable");
    tr = table.getElementsByTagName("tr");

    for (i = 0; i < tr.length; i++) {
        td = tr[i].getElementsByTagName("td")[0];
        if (td) {
            txtValue = td.textContent || td.innerText;
            if (txtValue.toUpperCase().indexOf(filter) > -1) {
                tr[i].style.display = "";
            } else {
                tr[i].style.display = "none";
            }
        }
    }
}

function profileImageFunction() {
    document.getElementById("image").style.cursor = "pointer";
}

function updateImageFunction(){
    var updateImageList = document.getElementsByClassName("updateImage");
    for (var i = 0; i < updateImageList.length; i++) {
        document.getElementById(updateImageList[i].id).style.cursor="pointer"
    }
}

$(document).ready(function(){
    $(".nav-tabs a").click(function(){
        $(this).tab('show');
    });
});

$(document).ready(function(){
    $("#givenNameUpdateImage").click(function(){
        $("#givenNameUpdate").toggle("slow");
    });
});

$(document).ready(function(){
    $("#surnameUpdateImage").click(function(){
        $("#surnameUpdate").toggle("slow");
    });
});

$(document).ready(function(){
    $("#emailUpdateImage").click(function(){
        $("#emailUpdate").toggle("slow");
    });
});

