data = csvread('20160927_045751_771.csv',1);
data = sortrows(data,1);
data = data(100:end,:);
data(:,1) = data(:,1)-data(1,1);

N = size(data,1);
t = linspace( 0, data(end,1), N );
[t vq] = sampleUniformly( data(:,1) ,data(:,2:end) );

data = [t' vq];

fs = 1000/mean(data(2:end,1)-data(1:end-1,1))
ref = [0,0,-1]; % vector pointing into the devices screen

% lfp
lpfn = 40;
%grav = filter( [1],ones(1,lpfn)./lpfn, data(:,[ACCX,ACCY,ACCZ]) );

grav = data(:, [GRAVX, GRAVY, GRAVZ]);

processOrientation(grav,ref);
