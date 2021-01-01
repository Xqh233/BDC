/*
 注入js关闭一些非必要的玩意
 
 10.28
*/

var openAppButton='open-app-btn m-space-float-openapp';
var spreadButton='spread-btn';

hide();
function hide(){
var clearButton=document.getElementsByClassName(openAppButton)[0];
clearButton.innerHTML='清除动态';
var list_archive=document.getElementsByClassName('archive-list')[0];
var list_album=document.getElementsByClassName('album-list')[0];
list_archive.parentNode.removeChild(list_archive);
list_album.parentNode.removeChild(list_album);

document.getElementsByClassName(spreadButton)[0].style.display='none';
document.getElementsByClassName('m-navbar')[0].style.display='none';
document.getElementsByClassName('tabs')[0].style.display='none';
document.getElementsByClassName('m-footer')[0].style.display='none';
}
