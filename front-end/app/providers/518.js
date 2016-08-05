const get_job_name = () => (document.querySelector('.job-title')||document.querySelector('.job_detail')).innerText.trim()
const get_company_name = () => (document.querySelector('.company-info h3')||document.querySelector('.comp-name')).innerText.trim()
const get_518_key = () => (document.querySelector('link[rel="canonical"]')||location).href.match(/job-(\d+)/)[1]

const provider = {
    get_job_name,
    get_company_name,
    get_518_key
}

export default provider