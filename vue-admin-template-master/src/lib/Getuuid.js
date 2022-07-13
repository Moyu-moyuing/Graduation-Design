class GetUUID {
    constructor(){}
    getuuid(){
        function id() {
            return (((1 + Math.random()) * 0x10000) | 0).toString(16).substring(1);
        }
        return (id() + id() + "-" + id() + "-" + id() + "-" + id() + "-" + id() + id() + id());
    }
}
export {GetUUID};