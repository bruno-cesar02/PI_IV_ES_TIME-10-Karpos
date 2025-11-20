exports.dashboard = (req, res) => {
  res.render('dashboard', {
    css: 'dashboard.css',
    title: 'Dashboard',
    msg: req.session.msg || ''
  });
}