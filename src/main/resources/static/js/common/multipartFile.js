/**
 * Attachment JS
 * @author Park-pro
 * @date 19.12.04
 */

function initialization($form) {
	var $file = $form.find('#file'),
		$files = $form.find('#files');
	
	$file.val('');
	$files.val('');
}

function ajaxForm($form, uploadSuccess) {
	$form.ajaxForm({
		beforeSend: function () {
			$("body").spin("modal");
		},
		uploadProgress: function (event, position, total, percentComplete) {},
		success: function () {},
		complete: uploadSuccess,
		error: function () {}
	});
}

function documentUpload(options) {
	
	var $form = $("form[name='formUpload']");
	
	$.ajaxSetup({
		dataType: "json",
		beforeSend: function () {
			$("body").spin("modal");
		},
		complete: function () {
			$("body").spin("modal");
		},
		error: function (jqxhr, textStatus, errorThrown) {
		}
	});
	
	/**
	 * init
	 */
	initialization($form);
	var	$inputFile = $form.find('#file');
	if (options !== null && options.multiple !== undefined && options.multiple) $inputFile = $form.find('#files');
	if (options !== null && options.accept !== undefined) $inputFile.attr('accept', options.accept);
	if (options !== null && options.position !== undefined) $form.find('input[name=position]').val(options.position);

	var uploadSuccess = function(responseText, statusText) {
		$("body").spin("modal");
		if (statusText === 'success') {
			var responseStatus = responseText.rs_st;
			var $file = $("input:file", $form);
			if (responseStatus === '1') return false;
			var data = responseText.responseJSON;
			if (typeof(options.callback) === 'function') options.callback(data, options);
			$file.val('');
			$file.attr("accept", "");
			$form.find('input[name=position]').val('');
		}
	};
	
	ajaxForm($form, uploadSuccess);
	
	$inputFile.one("change", function(e) {
		e.preventDefault();
		// 바이트 기준 1048576(Byte) = 1024(KB) = 1(MB)
		
		/** Migration 작업으로 인하여 파일용량 체크 주석
		var fileSize = document.getElementById("multipartFile").files[0].size;
		
		// 3MB
		if (fileSize > 3145728) {
			oneBtnModal(i18nProperty('alert.file.upload'));
			return false;
		}
		 **/
		
		$form.submit();
	});
	
	/**
	 * Hidden input File Button Click.
	 */
	$inputFile.click();
}

function summernoteUpload(file, editor) {
	var data = new FormData();
	data.append("file", file);

	$.ajax({
		data : data,
		type : "POST",
		url : "/attachment/upload",
		contentType : false,
		processData : false,
		success : function(data) {
			//항상 업로드된 파일의 url이 있어야 한다.
			$(editor).summernote('insertImage', data.url);
		}
	});
}