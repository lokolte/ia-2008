Type.registerNamespace('Koders.WebServices');
Koders.WebServices.MailingListService=function() {
Koders.WebServices.MailingListService.initializeBase(this);
this._timeout = 0;
this._userContext = null;
this._succeeded = null;
this._failed = null;
}
Koders.WebServices.MailingListService.prototype={
AddToMailingList:function(email,name,source,succeededCallback, failedCallback, userContext) {
return this._invoke(Koders.WebServices.MailingListService.get_path(), 'AddToMailingList',false,{email:email,name:name,source:source},succeededCallback,failedCallback,userContext); }}
Koders.WebServices.MailingListService.registerClass('Koders.WebServices.MailingListService',Sys.Net.WebServiceProxy);
Koders.WebServices.MailingListService._staticInstance = new Koders.WebServices.MailingListService();
Koders.WebServices.MailingListService.set_path = function(value) { Koders.WebServices.MailingListService._staticInstance._path = value; }
Koders.WebServices.MailingListService.get_path = function() { return Koders.WebServices.MailingListService._staticInstance._path; }
Koders.WebServices.MailingListService.set_timeout = function(value) { Koders.WebServices.MailingListService._staticInstance._timeout = value; }
Koders.WebServices.MailingListService.get_timeout = function() { return Koders.WebServices.MailingListService._staticInstance._timeout; }
Koders.WebServices.MailingListService.set_defaultUserContext = function(value) { Koders.WebServices.MailingListService._staticInstance._userContext = value; }
Koders.WebServices.MailingListService.get_defaultUserContext = function() { return Koders.WebServices.MailingListService._staticInstance._userContext; }
Koders.WebServices.MailingListService.set_defaultSucceededCallback = function(value) { Koders.WebServices.MailingListService._staticInstance._succeeded = value; }
Koders.WebServices.MailingListService.get_defaultSucceededCallback = function() { return Koders.WebServices.MailingListService._staticInstance._succeeded; }
Koders.WebServices.MailingListService.set_defaultFailedCallback = function(value) { Koders.WebServices.MailingListService._staticInstance._failed = value; }
Koders.WebServices.MailingListService.get_defaultFailedCallback = function() { return Koders.WebServices.MailingListService._staticInstance._failed; }
Koders.WebServices.MailingListService.set_path("/ws/1.0/account/mailinglist/default.asmx");
Koders.WebServices.MailingListService.AddToMailingList= function(email,name,source,onSuccess,onFailed,userContext) {Koders.WebServices.MailingListService._staticInstance.AddToMailingList(email,name,source,onSuccess,onFailed,userContext); }
