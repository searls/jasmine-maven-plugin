(function($) {

  $('.show-more').live('click',function(e) {
    e.preventDefault();
    $(this).closest('.expandable').find('.more').toggle('blind');
  });
})(jQuery);