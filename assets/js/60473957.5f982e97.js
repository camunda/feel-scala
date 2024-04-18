"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[1373],{4863:(e,t,n)=>{n.d(t,{Z:()=>s});var a=n(7462),l=n(7294),o=n(5671),r=n(3746);const s=e=>{let{children:t,onChange:n,language:s}=e;const i=(0,l.useRef)(null),[u,c]=(0,l.useState)(t),p=(0,l.useCallback)((e=>{const t=e.slice(0,-1);c(t),n(t)}),[]);return(0,o.Y)(i,p,{indentation:2}),l.createElement(r.ZP,(0,a.Z)({},r.lG,{code:u,language:s}),(e=>{let{className:t,style:n,tokens:a,getTokenProps:o}=e;return l.createElement("pre",{className:t,style:n,ref:i},a.map(((e,t)=>l.createElement(l.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>l.createElement("span",o({token:e,key:t})))),"\n"))))}))}},4294:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(7294),l=n(9669),o=n.n(l),r=n(4863),s=n(9537);const i=e=>{let{defaultExpression:t,feelContext:n,metadata:l,onResultCallback:i,onErrorCallback:u}=e;n&&(n=JSON.stringify(JSON.parse(n),null,2));const[c,p]=a.useState(t),[d,m]=a.useState(n),[g,h]=a.useState("<click 'Evaluate' to see the result of the expression>"),[f,v]=a.useState(null),[k,E]=a.useState(null),x=/^.+(?<line>\d+):(?<position>\d+).+$/gm,y=/^.+at position (?<position>\d+)$/gm;function b(e,t){h(null),v(e),E(t),u&&u(e)}return a.createElement("div",null,a.createElement("h2",null,"Expression"),a.createElement(r.Z,{onChange:p,language:"js"},c),n&&a.createElement("div",null,a.createElement("h2",null,"Context"),a.createElement("i",null,"A JSON document that is used to resolve ",a.createElement("strong",null,"variables")," ","in the expression."),a.createElement(r.Z,{onChange:m,language:"json"},d)),a.createElement("button",{onClick:function(){try{h("<evaluating the expression...>"),E(null);!function(e){o().post("https://feel.upgradingdave.com/api/v1/feel/evaluate",{expression:c,context:e,metadata:{...l}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t;if(null!=e&&null!=(t=e.data)&&t.error){var n,a;const t=e.data.error,l=x.exec(t);b({message:t,line:null==l||null==(n=l.groups)?void 0:n.line,position:null==l||null==(a=l.groups)?void 0:a.position},e.data.warnings)}else!function(e){v(null);const t=JSON.stringify(e.result);h(t),e.warnings.length>=1&&E(e.warnings);i&&i(t)}(e.data)}))}(n&&0!==d.trim().length?JSON.parse(d):{})}catch(t){var e;const n=y.exec(t.message);b({message:"failed to parse context: "+t.message,position:null==n||null==(e=n.groups)?void 0:e.position})}},className:"button button--primary button--lg"},"Evaluate"),a.createElement("br",null),a.createElement("br",null),a.createElement("h2",null,"Result"),a.createElement(s.Z,{title:(()=>{const e=null!=f&&f.line?" on line "+f.line:"",t=null!=f&&f.position?" at position "+f.position:"";return f&&"Error"+e+t})(),language:"json"},g||(null==f?void 0:f.message)),a.createElement("br",null),a.createElement("h2",null,"Warnings"),a.createElement(s.Z,null,(null==k?void 0:k.map(((e,t)=>a.createElement("li",{key:t},"[",e.type,"] ",e.message))))||"<none>"))}},3751:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>m,contentTitle:()=>p,default:()=>f,frontMatter:()=>c,metadata:()=>d,toc:()=>g});var a,l=n(7462),o=n(1880),r=(n(7294),n(3905)),s=n(4294),i=n(6518),u=n.n(i);const c={id:"tutorial-3-1",title:"3.1 Temporal expressions"},p=void 0,d={unversionedId:"tutorial/tutorial-3-1",id:"version-1.15/tutorial/tutorial-3-1",title:"3.1 Temporal expressions",description:"The next stop would put Zee in Cologne, as the journey continued there was an important",source:"@site/versioned_docs/version-1.15/tutorial/tutorial-3-1.mdx",sourceDirName:"tutorial",slug:"/tutorial/tutorial-3-1",permalink:"/feel-scala/docs/1.15/tutorial/tutorial-3-1",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/versioned_docs/version-1.15/tutorial/tutorial-3-1.mdx",tags:[],version:"1.15",frontMatter:{id:"tutorial-3-1",title:"3.1 Temporal expressions"},sidebar:"version-1.15/Tutorial",previous:{title:"2.1 String expressions",permalink:"/feel-scala/docs/1.15/tutorial/tutorial-2-1"},next:{title:"3.2 Temporal functions",permalink:"/feel-scala/docs/1.15/tutorial/tutorial-3-2"}},m={},g=[],h={toc:g};function f(e){let{components:t,...n}=e;return(0,r.kt)("wrapper",(0,l.Z)({},h,n,{components:t,mdxType:"MDXLayout"}),(0,r.kt)("p",null,"The next stop would put Zee in Cologne \ud83c\udde9\ud83c\uddea, as the journey continued there was an important\nquestion to answer: would Zee get in time for the conference? \u231a"),(0,r.kt)("p",null,"The trip started on September 15th, 2022, and since the journey began, around 200 hours had passed.\nCamundaCon will start on October 5th, 2022."),(0,r.kt)("p",null,"Let's use ",(0,r.kt)("a",{parentName:"p",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions/"},"temporal operators")," to check\nhow many days Zee has left:"),(0,r.kt)(s.Z,{defaultExpression:u()(a||(a=(0,o.Z)(['\n    // use temporal math to calculate the remaining days\n    date(startingDate) + duration("PT200H")']))),feelContext:'{"startingDate": "2022-09-15", "targetDate": "2022-10-05"}',metadata:{page:"tutorial-3-1"},mdxType:"LiveFeel"}),(0,r.kt)("details",null,(0,r.kt)("summary",null,"Solution"),(0,r.kt)("div",null,(0,r.kt)("div",null,"Zee has 11 days to arrive in Berlin."),(0,r.kt)("br",null),(0,r.kt)("pre",{title:"Expression"},'(date(targetDate) - date(startingDate) - duration("PT200H")) / duration("P1D")'))))}f.isMDXComponent=!0}}]);