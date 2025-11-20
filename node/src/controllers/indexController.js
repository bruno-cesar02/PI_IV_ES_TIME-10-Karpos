exports.index = (req, res) => {
  res.render('index', {
    css: 'index.css',
    title: 'Index'
  });
};