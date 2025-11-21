exports.verificarSeLogado = (req, res, next) => {
    console.log('Verificando se o usuário está logado...');
    if (req.session.user) {
        return next();
    } else {
        res.redirect('/login');
    }
}