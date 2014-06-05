
function pageBusy() {
  $('body').css('cursor','wait');
  $('#pleaseWaitDialog').show();
}

function pageFree() {
  $('body').css('cursor','default');
  $('#pleaseWaitDialog').hide();
}
