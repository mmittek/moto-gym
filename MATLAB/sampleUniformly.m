function [t vq] = sampleUniformly(x,y)
    xu = unique(x );
    ym = zeros(numel(xu), size(y,2) );
    for xi=1:numel(xu)
        idx = find( x==xu(xi) );
        ym(xi,:) = mean( y(idx,:),1 );
    end

    N = numel(xu);
    t = linspace(0, max(xu), N);
    vq = interp1(xu,ym,t);
end