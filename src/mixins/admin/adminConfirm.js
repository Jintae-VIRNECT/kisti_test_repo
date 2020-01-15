import Swal from 'sweetalert2/dist/sweetalert2.js'
// require('assets/style/plugin/sweetalert2/sweetalert2.scss')

var adminSwal = Swal.mixin({
    imageUrl:  require('assets/image/img-remote-logo@3x.png'),
    imageHeight: 70,
    showConfirmButton: true,
    showCancelButton: true,
    focusConfirm: true,
    focusCancel: false,
    reverseButtons: true,
    customContainerClass: 'admin-confirm-modal',
    customClass: {
        confirmButton: 'el-button el-button--solid',
        cancelButton: 'el-button el-button--default'
    },
    buttonsStyling: false
    // timer: 3000
})

const swalCancel = Swal.DismissReason.cancel
const swalBackdrop = Swal.DismissReason.backdrop

export default {
    methods: {
		// 로고 - 확인
        mx_logo_confirm(text, confirm) {
            adminSwal.fire({
                showCancelButton: false,
                confirmButtonText: confirm.text,
                title: text.title,
                html: text.text
            }).then(result => {
                if(result.value) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
                } else if (result.dismiss === swalBackdrop) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
				}
            })
		},
		// 로고 - 취소 확인
        mx_logo_cancel_confirm(text, cancel, confirm) {
            adminSwal.fire({
                confirmButtonText: confirm.text,
                cancelButtonText: cancel.text,
                title: text.title,
                html: text.text
            }).then(result => {
                if(result.value) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
                } else if (result.dismiss === swalCancel) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
                } else if (result.dismiss === swalBackdrop) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
				}
            })
		},
		// 기본 - 취소 확인
        mx_cancel_confirm(text, confirm, cancel) {
            adminSwal.fire({
				imageUrl: null,
				customContainerClass: 'admin-confirm-modal-title-text',
                confirmButtonText: confirm.text,
                cancelButtonText: cancel.text,
                title: text.title,
                html: text.text
            }).then(result => {
                if(result.value) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
                } else if (result.dismiss === swalCancel) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
                } else if (result.dismiss === swalBackdrop) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
				}
            })
		},
		// 간략 - 취소 확인
        mx_simple_cancel_confirm(text, confirm, cancel) {
            adminSwal.fire({
				imageUrl: null,
				customContainerClass: 'admin-confirm-modal-text',
                confirmButtonText: confirm.text,
                cancelButtonText: cancel.text,
                html: text
            }).then(result => {
                if(result.value) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
                } else if (result.dismiss === swalCancel) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
                } else if (result.dismiss === swalBackdrop) {
                    if(typeof cancel.action === 'function') {
                        cancel.action()
                    }
				}
            })
		},
		// 간략 - 확인
        mx_simple_confirm(text, confirm) {
            adminSwal.fire({
				imageUrl: null,
				customContainerClass: 'admin-confirm-modal-text',
				showCancelButton: false,
                confirmButtonText: confirm.text,
                html: text
            }).then(result => {
                if(result.value) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
                } else if (result.dismiss === swalBackdrop) {
                    if(typeof confirm.action === 'function') {
                        confirm.action()
                    }
				}
            })
        }
    },
}