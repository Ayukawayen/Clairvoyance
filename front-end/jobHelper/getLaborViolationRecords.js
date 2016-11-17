export default function getLaborViolationRecords(name, cb) {
    chrome.runtime.sendMessage({message:'listLaborViolationRecord'}, (cpRecords)=>{
        let result = [];
        for(let k in cpRecords) {
            if(!isCompanyNameMatch(k, name)) continue;
            
            result = result.concat(cpRecords[k].map((item)=>({
                package_id: null,
                name: k,
                date: item.日期.replace(/\//g,'-'),
                reason: `${item.條款}${item.內容}(${item.文號})`,
                link: item.url,
                snapshot: '',
            })));
        }
        cb(result);
    });
};

function isCompanyNameMatch(a, b) {
    a = a.replace(/\s+/g, '');
    b = b.replace(/\s+/g, '');
    
    if(a.length <= 0 || b.length <= 0) return false;
    
    let aNeedle = a.split('公司')[0] || a;
    let bNeedle = b.split('公司')[0] || b;
    
    if(a.indexOf(bNeedle) >= 0) return true;
    if(b.indexOf(aNeedle) >= 0) return true;
    
    return false;
};
