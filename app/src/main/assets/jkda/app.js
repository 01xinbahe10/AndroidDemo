function saveCurrentPageData() {
	var obj;
	switch(window.index) {
		case 1:
			{
				obj = page01();
			}
			break;

		case 2:
			{
				obj = page02();
			}
			break;

		case 3:
			{
				obj = page03();
			}
			break;

		case 4:
			{
				obj = page04();
			}
			break;

		case 5:
			{
				obj = page05();
			}
			break;
	}
	var jsonstr = JSON.stringify(obj);
	return [window.index, jsonstr];
}
