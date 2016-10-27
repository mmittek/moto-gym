function [data] = loadCSVAndPreprocess(csvFilename)
    % Load, sort, trim and adjust timestamps
    data = csvread(csvFilename,1);
    data = sortrows(data,1);
    data = data(1:end,:);
    data(:,1) = data(:,1)-data(1,1);

    % Average over milliseconds
    data(:,1) = ceil(data(:,1));
    t = unique(data(:,1));
    datam = zeros( numel(t), size(data,2) );
    for ti=1:numel(t)
        idx = find( data(:,1)==t(ti) );
        datam(ti,:) = mean(data(idx,:),1);
    end
    data = datam;

    % Quality control and threshold
    gps = data(:, end-3:end);
    idx = find( gps(:,4) < 12 & gps(:,1) ~=0  & gps(:,2) ~= 0);
    data = data(idx,:);
    data = data(2:end,:);   % skip first one

end