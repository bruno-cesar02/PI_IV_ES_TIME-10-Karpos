exports.redirecionarSeLogado = (req, res, next) => {
    if (req.session.user) {
        return res.redirect('/dashboard');
    }
    next();
}