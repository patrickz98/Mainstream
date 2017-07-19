var Utils = {};

Utils.getDate = function()
{
    console.log("############# getDate ################");

    const date = new Date();
    var day   = "" + date.getDate();
    var month = "" + (date.getMonth() + 1);
    var year  = date.getFullYear();

    if (day.length   == 1) day   = "0" + day;
    if (month.length == 1) month = "0" + month;

    return parseInt(year + month + day);
}


module.exports = Utils;
// export default Utils;
