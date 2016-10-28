function [dataf] = recursiveLPF(data,alpha)

    dataf = zeros( size(data) );
    dataf(1,:) = data(1,:);
    for i=2:size(data,1)
        dataf(i,:) = alpha.*data(i-1,:) + (1-alpha).*data(i,:);
    end

end