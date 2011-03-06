var MAX_SIDE_PANEL_WIDTH = 200;

// Handle resizing various parts of the page based on
// the screen size.
function onWindowResize() {
	var height = $(window).height();
	var width = $(window).width();
	$(".mainNav").width(width);
	$(".mainNav").height(height - 26);
	$("#main_nav_spacer_tab").width(width - 400);
	$("#manage_nav_spacer_tab").width(width - 400);
	
	var sidePanelPotentialWidth = width * .15;
	var sidePanelActualWidth = Math.min(MAX_SIDE_PANEL_WIDTH, sidePanelPotentialWidth);
	$(".filters_panel").width(sidePanelActualWidth);
	$(".help_panel").width(sidePanelActualWidth);
	$("#data_table").width(width - 16 - (sidePanelActualWidth * 2));
}
