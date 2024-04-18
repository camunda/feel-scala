"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[2695],{4863:(e,t,n)=>{n.d(t,{Z:()=>o});var a=n(7462),l=n(7294),r=n(5671),s=n(3746);const o=e=>{let{children:t,onChange:n,language:o}=e;const i=(0,l.useRef)(null),[u,c]=(0,l.useState)(t),p=(0,l.useCallback)((e=>{const t=e.slice(0,-1);c(t),n(t)}),[]);return(0,r.Y)(i,p,{indentation:2}),l.createElement(s.ZP,(0,a.Z)({},s.lG,{code:u,language:o}),(e=>{let{className:t,style:n,tokens:a,getTokenProps:r}=e;return l.createElement("pre",{className:t,style:n,ref:i},a.map(((e,t)=>l.createElement(l.Fragment,{key:t},e.filter((e=>!e.empty)).map(((e,t)=>l.createElement("span",r({token:e,key:t})))),"\n"))))}))}},4294:(e,t,n)=>{n.d(t,{Z:()=>i});var a=n(7294),l=n(9669),r=n.n(l),s=n(4863),o=n(9537);const i=e=>{let{defaultExpression:t,feelContext:n,metadata:l,onResultCallback:i,onErrorCallback:u}=e;n&&(n=JSON.stringify(JSON.parse(n),null,2));const[c,p]=a.useState(t),[d,m]=a.useState(n),[h,g]=a.useState("<click 'Evaluate' to see the result of the expression>"),[f,k]=a.useState(null),[v,E]=a.useState(null),x=/^.+(?<line>\d+):(?<position>\d+).+$/gm,y=/^.+at position (?<position>\d+)$/gm;function C(e,t){g(null),k(e),E(t),u&&u(e)}return a.createElement("div",null,a.createElement("h2",null,"Expression"),a.createElement(s.Z,{onChange:p,language:"js"},c),n&&a.createElement("div",null,a.createElement("h2",null,"Context"),a.createElement("i",null,"A JSON document that is used to resolve ",a.createElement("strong",null,"variables")," ","in the expression."),a.createElement(s.Z,{onChange:m,language:"json"},d)),a.createElement("button",{onClick:function(){try{g("<evaluating the expression...>"),E(null);!function(e){r().post("https://feel.upgradingdave.com/api/v1/feel/evaluate",{expression:c,context:e,metadata:{...l}},{headers:{accept:"*/*","content-type":"application/json"}}).then((e=>{var t;if(null!=e&&null!=(t=e.data)&&t.error){var n,a;const t=e.data.error,l=x.exec(t);C({message:t,line:null==l||null==(n=l.groups)?void 0:n.line,position:null==l||null==(a=l.groups)?void 0:a.position},e.data.warnings)}else!function(e){k(null);const t=JSON.stringify(e.result);g(t),e.warnings.length>=1&&E(e.warnings);i&&i(t)}(e.data)}))}(n&&0!==d.trim().length?JSON.parse(d):{})}catch(t){var e;const n=y.exec(t.message);C({message:"failed to parse context: "+t.message,position:null==n||null==(e=n.groups)?void 0:e.position})}},className:"button button--primary button--lg"},"Evaluate"),a.createElement("br",null),a.createElement("br",null),a.createElement("h2",null,"Result"),a.createElement(o.Z,{title:(()=>{const e=null!=f&&f.line?" on line "+f.line:"",t=null!=f&&f.position?" at position "+f.position:"";return f&&"Error"+e+t})(),language:"json"},h||(null==f?void 0:f.message)),a.createElement("br",null),a.createElement("h2",null,"Warnings"),a.createElement(o.Z,null,(null==v?void 0:v.map(((e,t)=>a.createElement("li",{key:t},"[",e.type,"] ",e.message))))||"<none>"))}},4455:(e,t,n)=>{n.r(t),n.d(t,{assets:()=>m,contentTitle:()=>p,default:()=>f,frontMatter:()=>c,metadata:()=>d,toc:()=>h});var a,l=n(7462),r=n(1880),s=(n(7294),n(3905)),o=n(4294),i=n(6518),u=n.n(i);const c={id:"chapter-5",title:"Chapter 5"},p=void 0,d={unversionedId:"learn/challenge/chapter-5",id:"learn/challenge/chapter-5",title:"Chapter 5",description:"The next stop would put Zee in Cologne, as the journey continued there was an important",source:"@site/docs/learn/challenge/chapter-5.mdx",sourceDirName:"learn/challenge",slug:"/learn/challenge/chapter-5",permalink:"/feel-scala/docs/next/learn/challenge/chapter-5",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/docs/learn/challenge/chapter-5.mdx",tags:[],version:"current",frontMatter:{id:"chapter-5",title:"Chapter 5"},sidebar:"Learn",previous:{title:"Chapter 4",permalink:"/feel-scala/docs/next/learn/challenge/chapter-4"},next:{title:"Chapter 6",permalink:"/feel-scala/docs/next/learn/challenge/chapter-6"}},m={},h=[],g={toc:h};function f(e){let{components:t,...n}=e;return(0,s.kt)("wrapper",(0,l.Z)({},g,n,{components:t,mdxType:"MDXLayout"}),(0,s.kt)("p",null,"The next stop would put Zee in Cologne \ud83c\udde9\ud83c\uddea, as the journey continued there was an important\nquestion to answer: would Zee get in time for the conference? \u231a"),(0,s.kt)("p",null,"The trip started on September 15th, 2022, and since the journey began, around 200 hours had passed.\nCamundaCon will start on October 5th, 2022."),(0,s.kt)("p",null,"Let's use ",(0,s.kt)("a",{parentName:"p",href:"https://docs.camunda.io/docs/components/modeler/feel/language-guide/feel-temporal-expressions/"},"temporal operators")," to check\nhow many days Zee has left:"),(0,s.kt)(o.Z,{defaultExpression:u()(a||(a=(0,r.Z)(['\n    // use temporal math to calculate the remaining days\n    date(startingDate) + duration("PT200H")']))),feelContext:'{"startingDate": "2022-09-15", "targetDate": "2022-10-05"}',metadata:{page:"challenge-5"},mdxType:"LiveFeel"}),(0,s.kt)("details",null,(0,s.kt)("summary",null,"Solution"),(0,s.kt)("div",null,(0,s.kt)("div",null,"Zee has 11 days to arrive in Berlin."),(0,s.kt)("br",null),(0,s.kt)("pre",{title:"Expression"},'(date(targetDate) - date(startingDate) - duration("PT200H")) / duration("P1D")'))))}f.isMDXComponent=!0}}]);