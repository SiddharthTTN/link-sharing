$(function(){
    $("#blah").hide()
});
function readURL(input) {
    var fileInput = input;
    var filePath = fileInput.value;
    var allowedExtensions = /(\.jpg|\.jpeg|\.png|\.gif)$/i;
    if(!allowedExtensions.exec(filePath)){
        $("#blah").hide() // try to hide google navigation bar
        alert('Please upload file having extensions .jpeg/.jpg/.png/.gif only.');
        fileInput.value = '';
        return false;
    }
    else {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            reader.onload = function (e) {
                $('#blah')
                    .attr('src', e.target.result);
            };
            $('#blah').show();
            reader.readAsDataURL(input.files[0]);
        }
    }
}