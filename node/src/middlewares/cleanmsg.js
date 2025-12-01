exports.cleanmsg = (req, res, next) => {
    if (req.session.msg) {
        req.session.msg = '';
    }
    next();
}