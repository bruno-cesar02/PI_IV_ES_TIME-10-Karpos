exports.dashboard = (req, res) => {
  res.render('dashboard', {
    css: 'dashboard.css',
    title: 'Dashboard',
    dados: req.session.user,
    msg: req.session.msg || ''
  });
}