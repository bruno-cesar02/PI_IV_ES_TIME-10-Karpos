exports.dados = (req, res) => {
  res.render('dados', {
    css: 'dados.css',
    title: 'Dados',
    msg: req.session.msg || ''
  });
}