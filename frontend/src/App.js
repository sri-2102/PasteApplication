import React, {useState} from 'react';

function App(){
  const [content, setContent] = useState('');
  const [ttl, setTtl] = useState('');
  const [maxViews, setMaxViews] = useState('');
  const [result, setResult] = useState(null);

  const API_BASE = process.env.REACT_APP_API_BASE || '';

  async function createPaste(e){
    e.preventDefault();
    const body = { content };
    if (ttl) body.ttl_seconds = parseInt(ttl,10);
    if (maxViews) body.max_views = parseInt(maxViews,10);
    try {
      const res = await fetch(`${API_BASE}/api/pastes`, {method:'POST', headers:{'Content-Type':'application/json'}, body: JSON.stringify(body)});
      const data = await res.json().catch(()=>({}));
      if (res.ok) setResult(data);
      else setResult({error: data.error || (`Server returned ${res.status}`)});
    } catch (err) {
      setResult({error: err.message || 'Network error'});
    }
  }

  return (
    <div style={{maxWidth:800,margin:'20px auto',fontFamily:'sans-serif'}}>
      <h1>Paste</h1>
      <form onSubmit={createPaste}>
        <div>
          <label>Content</label><br/>
          <textarea value={content} onChange={e=>setContent(e.target.value)} rows={10} style={{width:'100%'}} />
        </div>
        <div style={{marginTop:8}}>
          <label>TTL seconds (optional)</label><br/>
          <input value={ttl} onChange={e=>setTtl(e.target.value)} />
        </div>
        <div style={{marginTop:8}}>
          <label>Max views (optional)</label><br/>
          <input value={maxViews} onChange={e=>setMaxViews(e.target.value)} />
        </div>
        <div style={{marginTop:12}}>
          <button type="submit">Create Paste</button>
        </div>
      </form>
      {result && (
        <div style={{marginTop:20}}>
          {result.error ? <div style={{color:'red'}}>Error: {result.error}</div> : <div>Paste created: <a href={result.url}>{result.url}</a></div>}
        </div>
      )}
    </div>
  )
}

export default App;
