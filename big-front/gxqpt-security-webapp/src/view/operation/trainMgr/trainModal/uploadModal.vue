<template>
    <Modal v-model="showModal" title="上传" width="800px" :mask-closable="false">
        <div style="text-align:center">
            <Form ref="uploadRef" :label-width="100" class="clearfix" :model="formData" :rules="formRules">
                <FormItem label="培训签到表" prop="signFileList" required>
                    <hyUpload
                        ref="signUpload"
                        action="/api/file/p/file/simple"
                        :onSuccess="signTableSuccess"
                        :default-file-list="defaultSignFiles"
                        :on-remove="signTableRemove"/>
                </FormItem>
                <FormItem label="培训反馈表">
                    <hyUpload
                        ref="feedbackUpload"
                        action="/api/file/p/file/simple"
                        :onSuccess="feedbackSuccess"
                        :default-file-list="defaultFeedbackFiles"
                        :format="['xls','xlsx','doc', 'docx','pdf']"
                        :on-remove="feedbackRemove"/>
                </FormItem>
            </Form>
        </div>
        <div slot="footer">
            <Button class="modalBtn" type="primary" @click="handleSubmit" size="large">确定</Button>
            <Button class="modalBtn" type="default" @click="showModal = false" size="large">关闭</Button>
        </div>
    </Modal>
</template>

<script>
// 文件上传
import hyUpload from '@/components/hengyun/hyUpload'
const FILE_BUSS_TYPE = {
    // 签到表
    SIGN: 'train_sign_in',
    // 反馈表
    FEEDBACK: 'train_feedback'
}
export default {
    components: {
        hyUpload
    },
    data(){
        const vm = this
        return{
            // 更新时要删除的文件的id
            removeList: [],
            showModal: false,
            // 默认签到表
            defaultSignFiles: [],
            // 默认反馈表
            defaultFeedbackFiles: [],
            // 操作的行的id
            id: '',
            formData: {
                signFileList: [],
                feedbackFileList: []
            },
            formRules: {
                signFileList: [{validator: (rule, val, cb) => {
                    if (vm.formData.signFileList.length == 0) {
                        cb(new Error('签到表必传'))
                        return
                    }
                    cb()
                }}]
            }
        }
    },
    methods: {
        handleSubmit() {
            const vm = this
            vm.$refs.uploadRef.validate(valid => {
                if (valid) {
                    const files = []
                    // 签到表
                    vm.formData.signFileList.map(item => {
                        const file = item.response.data.list[0]
                        files.push({
                            bussId: vm.id,
                            fileId: file.id,
                            fileSize: file.size,
                            fileUrl: file.url,
                            name: file.submittedFileName,
                            type: file.ext,
                            bussType: FILE_BUSS_TYPE.SIGN
                        })
                    })
                    // 反馈表
                    vm.formData.feedbackFileList.map(item => {
                        const file = item.response.data.list[0]
                        files.push({
                            bussId: vm.id,
                            fileId: file.id,
                            fileSize: file.size,
                            fileUrl: file.url,
                            name: file.submittedFileName,
                            type: file.ext,
                            bussType: FILE_BUSS_TYPE.FEEDBACK
                        })
                    })
                    vm.$emit('on-ok', files, vm.removeList)
                    vm.showModal = false
                } else {
                    vm.$Message.error('表单验证失败')
                }
            })
        },
        signTableSuccess(resp, file, fileList) {
            this.formData.signFileList = fileList
        },
        signTableRemove(file, fileList) {
            // this.removeList.push(file.response.data.list[0].id)
            this.formData.signFileList = fileList
        },
        feedbackSuccess(resp, file, fileList) {
            this.formData.feedbackFileList = fileList
        },
        feedbackRemove(file, fileList) {
            // this.removeList.push(file.response.data.list[0].id)
            this.formData.feedbackFileList = fileList
        },
        open({id, files}) {
            files = files || []
            this.id = id
            // this.removeList = []
            this.defaultSignFiles = []
            this.defaultFeedbackFiles = []
            this.$refs.signUpload.clearFiles()
            this.$refs.feedbackUpload.clearFiles()
            this.showModal = true
            this.$refs.uploadRef.resetFields()
            const signFile = []
            const feedbackFile = []
            const removeList = []
            files.map(({fileId, fileSize, fileUrl, fileName, fileType, bussType}) => {
                removeList.push(String(fileId))
                if (bussType == FILE_BUSS_TYPE.SIGN) {
                    signFile.push({
                        url: fileUrl,
                        name: fileName,
                        // 这里的组装是为了上传时取值的结构一致
                        response: {
                            data: {
                                list: [{
                                    id: fileId,
                                    size: fileSize,
                                    url: fileUrl,
                                    submittedFileName: fileName,
                                    ext: fileType,
                                    name: fileName,
                                } || {}]
                            }
                        }
                    })
                } else {
                    feedbackFile.push({
                        url: fileUrl,
                        name: fileName,
                        // 这里的组装是为了上传时取值的结构一致
                        response: {
                            data: {
                                list: [{
                                    id: fileId,
                                    size: fileSize,
                                    url: fileUrl,
                                    submittedFileName: fileName,
                                    ext: fileType,
                                    name: fileName,
                                } || {}]
                            }
                        }
                    })
                }
            })
            var signObj = {}
            const signFiles = signFile.reduce(function(item, next) {
                signObj[next.key] ? '' : signObj[next.key] = true && item.push(next)
                return item
            }, [])
            var feedbackObj = {}
            const feedbackFiles = feedbackFile.reduce(function(item, next) {
                feedbackObj[next.key] ? '' : feedbackObj[next.key] = true && item.push(next)
                return item
            }, [])
            console.log(signFiles)
            console.log(feedbackFiles)
            this.removeList = removeList
            this.defaultSignFiles = signFiles || []
            this.defaultFeedbackFiles = feedbackFiles || []
            this.formData.signFileList = signFiles
            this.formData.feedbackFileList = feedbackFiles
        }
    }
}
</script>

<style scoped>
.ivu-form-item >>> .ivu-form-item-content{
    height:auto;
}
.ivu-input-wrapper >>> textarea{
    resize: none;
}
.download,.download:hover{
    width: 100px;
    height: 32px;
    line-height: 30px;
    color: #000;
    float: left;
}
</style>
