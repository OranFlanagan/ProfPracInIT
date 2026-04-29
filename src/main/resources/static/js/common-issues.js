const fixCache = {};

async function toggleFix(card) {
    const panel = card.querySelector('.fix-panel');
    const issueId = card.dataset.issueId;
    const productId = card.dataset.productId;

    if (panel.style.display === 'block') {
        panel.style.display = 'none';
        return;
    }

    if (fixCache[issueId]) {
        panel.innerHTML = fixCache[issueId];
        panel.style.display = 'block';
        return;
    }

    panel.innerHTML = '<p>Loading...</p>';
    panel.style.display = 'block';

    try {
        const res = await fetch(`/api/issues/${issueId}/fixes`);
        const fixes = await res.json();

        const fixText = fixes.length
            ? `<p>${fixes[0].fixDescription}</p>`
            : `<p>No fix available yet.</p>`;

        const html = `
            ${fixText}
            <div class="fix-feedback">
                <p class="fix-feedback-label">Did this fix your issue?</p>
                <div class="fix-feedback-buttons">
                    <button class="btn-resolved" 
                        data-issue-id="${issueId}" 
                        data-product-id="${productId}"
                        onclick="issueResolved(this)">Issue resolved</button>
                    <button class="btn-problems" onclick="window.location.href='/email-form?issueId=${issueId}'">Still having problems</button>
                </div>
            </div>
        `;

        fixCache[issueId] = html;
        panel.innerHTML = html;

    } catch (err) {
        panel.innerHTML = '<p>Could not load fix.</p>';
    }
}

async function issueResolved(btn) {
    const issueId = btn.dataset.issueId;
    const productId = btn.dataset.productId;

    await fetch('/common-issues/deflection-success', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            issueId: Number(issueId),
            productId: Number(productId)
        })
    });

    const panel = btn.closest('.fix-panel');
    panel.innerHTML = '<p>Glad that sorted it!</p>';
}