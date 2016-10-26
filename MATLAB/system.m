% Load, sort, trim and adjust timestamps
data = csvread('2016-09-25 22_27_17_260.csv',1  );
data = sortrows(data,1);
data = data(500:end,:);
data(:,1) = (data(:,1)-min(data(:,1)))./1000000;    % get down to milliseconds

% Average over milliseconds
data(:,1) = ceil(data(:,1));
t = unique(data(:,1));
datam = zeros( numel(t), size(data,2) );
for ti=1:numel(t)
    idx = find( data(:,1)==t(ti) );
    datam(ti,:) = mean(data(idx,:),1);
end
data = datam;


[x,y,z] = gpsToXYZ( data(:,18), data(:,19), 0 );
figure(1)
clf
plot(x-x(1),y-y(1), 'rx');
axis equal;


