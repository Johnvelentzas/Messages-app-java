
/**
 * The Server Acess Protocol is used to comunicate with the server.
 */
public enum SAP {
    
    greet,  //The begining state of the server. Expects a client name.
    await,  //The default state of the server. When it is on await it expects to receive an SAP code.
    ok,     //A signal given by the server to proceed with the request.
    prc,    //Proceed with the client request.
    bdr,    //Bad Request.
    cun,    //Change User Name.
    otl,    //Open text log. Optional parameters <+int>, <-int>, 0(default). ex. "otl:-10" the client will receive the 10 last lines of the log file if they exist. Follows the directory of the text file if given the ok by the server.
    atl,    //Add to Text Log.
    rtl,    //Remove from Text Log. Follows void.
    stl,    //Store Text Log. Follows void.
    ctl,    //Create Text Log. Follows void.
    mun,    //Message User with Name. Mandatory parameter <String> the user name of the message recepient. Follows the message of the user.
    ocu,    //Open Conversation with User. Mandatory parameter <String> the user name of the message recepient. Follows void.
    moc,    //Message Open Conversation. Follows the message of the user.
    uoc,    //Update Open Conversation. Follows void.
}
